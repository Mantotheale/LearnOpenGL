#version 330

struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
};

struct Light {
    vec3 position;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

in vec3 normal;
in vec3 fragPos;

out vec4 FragColor;

uniform Material material;
uniform Light light;

uniform vec3 lightPosition;
uniform vec3 viewerPosition;

void main() {
    vec3 ambient = light.ambient * material.ambient;

    vec3 norm = normalize(normal);
    vec3 lightDirection = normalize(light.position - fragPos);
    float diffuseCoefficient = max(dot(norm, lightDirection), 0);
    vec3 diffuse = light.diffuse * (diffuseCoefficient * material.diffuse);

    vec3 viewDirection = normalize(viewerPosition - fragPos);
    vec3 reflectDirection = reflect(-lightDirection, norm);
    float specularCoefficient = pow(max(dot(viewDirection, reflectDirection), 0), material.shininess);
    vec3 specular = light.specular * (specularCoefficient * material.specular);

    vec3 result = ambient + diffuse + specular;
    FragColor = vec4(result, 1.0);

    vec3 a = lightPosition;
}