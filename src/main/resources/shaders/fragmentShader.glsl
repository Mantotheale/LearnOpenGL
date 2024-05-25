#version 330 core
out vec4 FragColor;

in vec2 texCoords;

uniform sampler2D texture1;
uniform float near;
uniform float far;

float linearizeDepth(float depth) {
    float z = depth * 2.0 - 1.0;
    return (2.0 * near * far) / (far + near - z * (far - near));
}

void main() {
    FragColor = texture(texture1, texCoords);
    //float depth = linearizeDepth(gl_FragCoord.z) / far;
    //FragColor = vec4(vec3(depth), 1);
}