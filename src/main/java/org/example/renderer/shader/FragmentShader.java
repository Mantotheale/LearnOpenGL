
package org.example.renderer.shader;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;

public class FragmentShader extends Shader {
    public FragmentShader(String pathString) {
        super(GL_FRAGMENT_SHADER, pathString);
    }
}
