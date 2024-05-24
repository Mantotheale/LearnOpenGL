#version 330

struct Material {
    sampler2D diffuse;
    sampler2D specular;
    float shininess;
};

struct Light {
    vec3 position;
    vec3 direction;
    float cutoff;
    float outerCutoff;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    float constant;
    float linear;
    float quadratic;
};

in vec3 normal;
in vec3 fragPos;
in vec2 texCoord;

out vec4 FragColor;

uniform Material material;
uniform Light light;

uniform vec3 viewerPosition;

void main() {
    vec3 norm = normalize(normal);
    vec3 lightDirection = normalize(light.position - fragPos);

    float theta = dot(lightDirection, -light.direction);
    float epsilon = light.cutoff - light.outerCutoff;
    float intensity = clamp((theta - light.outerCutoff) / epsilon, 0.0, 1.0);

    vec3 ambient = light.ambient * vec3(texture(material.diffuse, texCoord));

    float diffuseCoefficient = max(dot(norm, lightDirection), 0);
    vec3 diffuse = light.diffuse * diffuseCoefficient * vec3(texture(material.diffuse, texCoord));

    vec3 viewDirection = normalize(viewerPosition - fragPos);
    vec3 reflectDirection = reflect(-lightDirection, norm);
    float specularCoefficient = pow(max(dot(viewDirection, reflectDirection), 0), material.shininess);
    vec3 specular = light.specular * specularCoefficient * vec3(texture(material.specular, texCoord));

    float distance = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));

    vec3 result = intensity * attenuation * (diffuse + specular) + ambient;
    FragColor = vec4(result, 1.0);
}