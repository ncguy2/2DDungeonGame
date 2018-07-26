package net.ncguy.material;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public abstract class EmptyMaterial {

    public String name;

    public EmptyMaterial(String name) {
        this.name = name;
    }

    public abstract void Bind(ShaderProgram shader);


}
