# Coelho Manager (Designations & Reports Generator)

Sistema para gerenciamento e geração de designações padronizadas para circuitos e emissão automatizada de relatórios de tráfego (OCR) com envio por e-mail.

## 🚀 Funcionalidades

- **Gerador de Designações**: Geração padronizada de códigos de identificação de circuitos com base no CNL e tipo de link.
- **Consulta de CNLs**: Visualização e consulta de Códigos Nacionais de Localidades (CNL).
- **Gerador de Relatórios ISP**: Leitura de imagens de tráfego através de OCR (Tesseract), cálculo de 95º percentil e geração de relatórios em PDF com envio automático de e-mails.
- **Sincronização com Netbox**: Integração via API REST para sincronização de circuitos de clientes.

---

## ⚙️ Configuração de Variáveis de Ambiente

Antes de executar o projeto, configure as variáveis de ambiente necessárias. Você pode se basear nos arquivos `.env.example`.

### Backend (`coelho.manager.back/coelho.manager`)

| Variável | Descrição | Exemplo |
| :--- | :--- | :--- |
| `NETBOX_URL` | URL da API do Netbox | `http://{SEU_DOMINIO_NETBOX}/api/circuits/circuits/?type=ccl` |
| `NETBOX_TOKEN` | Token de autenticação da API Netbox | `{SEU_NETBOX_API_TOKEN}` |
| `MAIL_FROM` | Endereço de e-mail remetente | `noc@{SEU_DOMINIO}.com` |
| `SMTP_HOST` | Host do servidor SMTP | `smtp.{SEU_DOMINIO}.com` |
| `SMTP_PORT` | Porta do servidor SMTP | `587` |
| `SMTP_USER` | Usuário de autenticação SMTP | `usuario@{SEU_DOMINIO}.com` |
| `SMTP_PASSWORD` | Senha de autenticação SMTP | `{SUA_SENHA_SMTP}` |
| `IMAGES_REPOSITORY_PATH` | Diretório para arquivos temporários de imagens | `./temp-images/` ou `C:/reports/temp-images/` |
| `TESSDATA_PATH` | Caminho dos dados de linguagem do Tesseract OCR | `C:/Program Files/Tesseract-OCR/tessdata/` |
| `LOG_PATH` | Diretório para gravação dos logs | `./logs` |

### Frontend (`coelho.manager.front/coelho.manager`)

| Variável | Descrição | Exemplo |
| :--- | :--- | :--- |
| `VITE_API_URL` | URL base da API do backend | `http://localhost:8080/api` |

---

## 🛠️ Como Executar

### Backend (Spring Boot / Java 21)
```bash
cd coelho.manager.back/coelho.manager
./mvnw spring-boot:run
```

### Frontend (React + Vite + TypeScript)
```bash
cd coelho.manager.front/coelho.manager
npm install
npm run dev
```
