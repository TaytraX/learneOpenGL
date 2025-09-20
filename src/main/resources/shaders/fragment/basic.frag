#version 330 core

out vec4 FragColor;

in vec2 TexCoord;
uniform sampler2D texture1;
uniform sampler2D texture2;

void main()
{
    vec4 tex1 = texture(texture1, TexCoord);  // Sol en pierre (base)
    vec4 tex2 = texture(texture2, TexCoord);  // Image par-dessus

    // texture2 par-dessus texture1 selon la transparence de texture2
    FragColor = mix(tex1, tex2, tex2.a);
}