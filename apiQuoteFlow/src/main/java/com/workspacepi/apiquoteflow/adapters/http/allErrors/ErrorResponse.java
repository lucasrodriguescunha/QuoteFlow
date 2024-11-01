package com.workspacepi.apiquoteflow.adapters.http.allErrors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

//  Propriedades especificas das exception
    @JsonProperty("id")
    @JsonIgnore
    private UUID id;

    @JsonProperty("id_produto")
    @JsonIgnore
    private UUID id_produto;

    @JsonProperty("email")
    private String email;

//  Propriedades genericas
    @JsonProperty("mensagem")
    private String mensagem;

    @JsonProperty("status")
    private int status;

//  Construtores para cada exception

//  Exception de não encontrado
    public ErrorResponse(String mensagem, UUID id, int status) {
        this.mensagem = mensagem;
        this.id = id;
        this.status = status;
    }

//  Exception de email já cadastrado
    public ErrorResponse(String mensagem, String email, int status) {
        this.email = email;
        this.mensagem = mensagem;
        this.status = status;
    }

    //  Exception generica
    public ErrorResponse(String mensagem, int status) {
        this.mensagem = mensagem;
        this.status = status;
    }

}