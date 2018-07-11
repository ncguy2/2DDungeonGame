package net.ncguy.entity.component.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import net.ncguy.entity.component.HealthComponent;
import net.ncguy.util.Shaders;

public class HealthUIComponent extends UIComponent<HealthComponent> {

    Texture texture;

    public HealthUIComponent(String name, HealthComponent targetComponent) {
        super(name, targetComponent);
    }

    @Override
    public void Render(SpriteBatch batch) {
        if(texture == null)
            texture = new Texture(Gdx.files.internal("textures/progressbar.png"));

        ShaderProgram shader = batch.getShader();
        batch.setShader(Shaders.progressBarShader);
        Shaders.progressBarShader.setUniformf("u_colours.Empty", 1, 1, 1, .4f);
        Shaders.progressBarShader.setUniformf("u_colours.Full", 0, 1, 0, 1);
        Shaders.progressBarShader.setUniformf("u_colours.Overfill", 1, 0, 1, 1);

        float percValue = targetComponent.health.health / targetComponent.health.maxHealth;
        float overfill = targetComponent.health.tempHealth / targetComponent.health.maxHealth;

        Shaders.progressBarShader.setUniformf("value", percValue);
        Shaders.progressBarShader.setUniformf("overfill", overfill);

        float x = 0;
        float y = 40;
        float w = 56;
        float h = 8;

        Vector2 pos = new Vector2();
        transform.WorldTransform().getTranslation(pos);
        pos.add(x, y);
        pos.x -= w * .5f;

        batch.draw(texture, pos.x, pos.y, w, h);
        batch.setShader(shader);
    }
}
