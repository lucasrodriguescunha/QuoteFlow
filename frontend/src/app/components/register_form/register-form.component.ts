import { CommonModule } from '@angular/common';
// Importa o módulo comum que fornece diretivas comuns, como ngIf e ngFor.
import { Component } from '@angular/core';
// Importa o decorador 'Component', que é usado para definir um componente Angular.
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
// Importa classes e módulos necessários para trabalhar com formulários reativos em Angular,
// incluindo FormBuilder para criar instâncias de FormGroup e Validators para validação de campos.
import { Router, RouterModule } from '@angular/router';
// Importa o Router para navegação entre rotas e RouterModule para definir as rotas da aplicação.
import { NgxMaskDirective, NgxMaskPipe } from 'ngx-mask';
// Importa diretivas e pipes do pacote 'ngx-mask' para aplicar máscaras a campos de entrada.
import { ApiAuthService } from "../../services/api-auth.service";
import {Usuario} from "../../models/user.model";
// Importa o serviço 'ApiAuthService' que gerencia a comunicação com a API para registro de usuários.

@Component({
  selector: 'app-register-form',
  standalone: true,
  // Define este componente como um componente independente que pode ser usado sem ser declarado em um módulo.
  imports: [
    RouterModule,
    NgxMaskDirective,
    NgxMaskPipe,
    ReactiveFormsModule,
    CommonModule,
  ],
  // Lista os módulos e diretivas que serão utilizados neste componente.
  templateUrl: './register-form.component.html',
  styleUrls: ['./register-form.component.css'],
  // Especifica o caminho do arquivo HTML e CSS para este componente.
})
export class RegisterFormComponent {
  registerForm: FormGroup;
  isLoading = false;
  errorMessage: any;

  constructor(private fb: FormBuilder, private router: Router, private apiService: ApiAuthService) {
    this.registerForm = this.fb.group({
      nome: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  register() {
    if (this.registerForm.valid) {
      this.isLoading = true;
      const usuarioData: Usuario = this.registerForm.value;
      console.log('Enviando dados do usuário:', usuarioData);
      this.apiService.registerUser(usuarioData).subscribe(
        response => {
          console.log('Resposta da API:', response);
          this.isLoading = false;
          alert("Usuário registrado com sucesso!");
          this.router.navigate(['/dashboard/notifications']);
        },
        error => {
          console.error('Erro ao registrar o usuário:', error);
          this.isLoading = false;
          if (error.status === 409) {
            this.errorMessage = 'Este email já está registrado. Por favor, utilize outro.';
          } else {
            this.errorMessage = 'Falha no registro. Tente novamente.';
          }
        }
      );
    } else {
      this.registerForm.markAllAsTouched();
      console.log('Formulário inválido:', this.registerForm.errors);
    }
  }
}
