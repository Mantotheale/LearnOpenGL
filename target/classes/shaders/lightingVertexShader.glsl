#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 vertex_normal;
layout (location = 2) in vec2 textureCoordinates;

out vec3 normal;
out vec3 fragPos;
out vec2 texCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    normal = mat3(transpose(inverse(model))) * vertex_normal;
    fragPos = vec3(model * vec4(position, 1));
    texCoord = textureCoordinates;

    gl_Position = projection * view * model * vec4(position, 1);
}
