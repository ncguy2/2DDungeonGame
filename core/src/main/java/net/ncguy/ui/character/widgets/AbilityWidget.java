package net.ncguy.ui.character.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import net.ncguy.ability.Ability;
import net.ncguy.assets.Sprites;
import net.ncguy.entity.Entity;
import net.ncguy.util.StringUtils;

import java.util.Optional;

public abstract class AbilityWidget extends VisTable {

    public final Entity owningEntity;
    Ability ability;

    VisImage image;
    VisLabel label;
    AbilityTooltipWidget tooltipWidget;
    Tooltip tooltip;

    public AbilityWidget(Entity owningEntity) {
        this.owningEntity = owningEntity;
        Init();
    }

    public void SetAbility(Ability ability) {
        this.ability = ability;
        Repopulate();
    }

    public void Init() {
        setBackground("window-border-bg");

        image = new VisImage();
        label = new VisLabel();
        tooltipWidget = new AbilityTooltipWidget(this);
        tooltip = new Tooltip.Builder(tooltipWidget).target(this)
                .build();

        setTouchable(Touchable.enabled);

        float size = 64f;

        add(image).pad(4).minSize(size).maxSize(size).size(size).grow().row();
        add(label).growX().row();

        InitDnD();
        Repopulate();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public abstract void InitDnD();

    public void NullPopulate() {
        image.setDrawable(Sprites.Pixel()
                .getTexture());
        label.setText("");
    }

    public void Repopulate() {
        if (ability == null) {
            NullPopulate();
            return;
        }

        if (!StringUtils.IsNullOrEmpty(ability.icon)) {
            FileHandle handle = Gdx.files.internal(ability.icon);
            if (handle.exists() && !handle.isDirectory()) {
                Texture texture = new Texture(handle);
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                image.setDrawable(texture);
            }
        }
        label.setText(ability.name);
        tooltipWidget.Update();
    }

    public static class AbilityTooltipWidget extends VisTable {
        AbilityWidget attachedWidget;

        VisLabel name;
        VisLabel level;

        public AbilityTooltipWidget(AbilityWidget attachedWidget) {
            this.attachedWidget = attachedWidget;
            Init();
            Update();
        }

        void Init() {
            name = new VisLabel();
            level = new VisLabel();

            defaults().growX();
            columnDefaults(0).right().padRight(4);
            columnDefaults(1).left().padLeft(4);

            add(name).row();
            add(level).row();
        }

        public Optional<Ability> Ability() {
            return Optional.ofNullable(attachedWidget.ability);
        }

        public void Update() {
            Ability().map(a -> a.name)
                    .ifPresent(name::setText);
            Ability().map(a -> a.level)
                    .map(Enum::name)
                    .map(StringUtils::ToDisplayCase)
                    .ifPresent(level::setText);

            BitmapFont font = name.getStyle().font;
            GlyphLayout glyphLayout = new GlyphLayout();
            float width = 0;
            glyphLayout.setText(font, name.getText());
            width = Math.max(glyphLayout.width, width);
            glyphLayout.setText(font, level.getText());
            width = Math.max(glyphLayout.width, width);

            setWidth(width);
            Group parent = getParent();
            if(parent != null)
                parent.setWidth(width + 8);
        }
    }

}
