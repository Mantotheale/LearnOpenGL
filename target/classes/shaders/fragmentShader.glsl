#version 330

in vec3 normal;
in vec3 fragPos;

out vec4 FragColor;

uniform vec3 objectColor;
uniform vec3 lightColor;
uniform vec3 lightPosition;

void main() {
    float ambientStrength = 0.1;
    vec3 ambient = ambientStrength * lightColor;

    vec3 norm = normalize(normal);
    vec3 lightDirection = normalize(lightPosition - fragPos);
    float diffuseCoefficient = max(dot(norm, lightDirection), 0);
    vec3 diffuse = lightColor * diffuseCoefficient;

    vec3 result = (ambient + diffuse) * objectColor;
    FragColor = vec4(result, 1.0);

    vec3 a = lightPosition;
}