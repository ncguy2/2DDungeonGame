package net.ncguy.tools.debug.view;

import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import net.ncguy.particles.ParticleBlock;
import net.ncguy.particles.ParticleManager;
import net.ncguy.particles.ParticleProfile;
import net.ncguy.particles.ParticleShader;
import net.ncguy.tools.debug.items.ParticleItemController;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParticleController implements Initializable {

    public Accordion container;
    public ListView<ParticleBlock> availableBlockList;
    public CodeArea spawnScriptDisplay;
    public CodeArea updateScriptDisplay;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ParticleManager.instance().Blocks(availableBlockList.getItems()::add);
        ParticleManager.instance().Profiles(p -> {
            FXMLItem item = new FXMLItem(p.name, "/fxml/items/particleItem.fxml", false);
            Optional<FXMLItem.ItemInfo> build = item.Build();
            build.ifPresent(info -> {
                if(info.controller instanceof ParticleItemController)
                    ((ParticleItemController) info.controller).SetProfile(p);
                info.node.setUserData(info.controller);
                container.getPanes().add((TitledPane) info.node);
            });
        });

        container.expandedPaneProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null)
                return;
            Object userData = newValue.getUserData();
            if(userData instanceof ParticleItemController) {
                ParticleItemController ctrlr = (ParticleItemController) userData;
                SelectProfile(ctrlr.GetProfile());
            }
        });

        Setup(spawnScriptDisplay);
        Setup(updateScriptDisplay);

    }

    public void SelectProfile(ParticleProfile profile) {
        Map<ParticleBlock.Type, ParticleShader> shaders = ParticleShader.FromProfile(profile, false);

        ParticleShader spawnShader = shaders.get(ParticleBlock.Type.Spawn);
        ParticleShader updateShader = shaders.get(ParticleBlock.Type.Update);

        String spawnScript = spawnShader.GetScript();
        String updateScript = updateShader.GetScript();


        SetText(spawnScriptDisplay, spawnScript);
        SetText(updateScriptDisplay, updateScript);

    }

    void Setup(CodeArea area) {
        area.setParagraphGraphicFactory(LineNumberFactory.get(area));
        area.getStylesheets().add("css/glHighlight.css");
    }

    void SetText(CodeArea area, String text) {
        area.replaceText(text);
        area.setStyleSpans(0, computeHighlighting(area.getText()));
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                        matcher.group("DATA") != null ? "data" :
                            matcher.group("PAREN") != null ? "paren" :
                                matcher.group("PREPRO") != null ? "preprocessor" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }


    private static final String[] KEYWORDS = new String[] {
            "uniform", "in", "out", "inout", "attribute",
            "layout", "varying", "return", "discard"
    };

    private static final String[] DataTypes = new String[] {
            "float", "int", "sampler1D", "bool", "void",
            "vec2", "ivec2", "sampler2D",
            "vec3", "ivec3", "sampler3D",
            "vec4", "ivec4"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String DATA_PATTERN = "\\b(" + String.join("|", DataTypes) + ")\\b";
    private static final String PREPROCESSOR_PATTERN = "\\^(#)\\S*";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                + "(?<DATA>" + DATA_PATTERN + ")"
                + "(?<PREPRO>" + PREPROCESSOR_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

}
