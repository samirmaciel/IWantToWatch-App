# WantToWatch

MVP Android em Kotlin e Jetpack Compose para descobrir títulos e manter uma lista local de filmes e séries.

## Configurar o catálogo TMDB

Crie uma chave de API no TMDB e adicione ao `gradle.properties` do usuário (ou ao `gradle.properties` local, sem versionar credenciais):

```properties
TMDB_API_KEY=sua_chave
```

O app usa os endpoints de tendências, mais bem avaliados e busca do TMDB. Sem a chave, os recursos locais continuam funcionando e a Home explica como configurar a descoberta. A API retorna os metadados em português (`pt-BR`); os posters usam `image.tmdb.org`.

## Executar

Abra o projeto no Android Studio e aguarde a sincronização Gradle. Ou use:

```powershell
.\gradlew.bat :app:assembleDebug
```

Requer Android SDK 36 e JDK 21. Para instalar pelo Android Studio, execute a configuração `app` em um emulador ou dispositivo Android 10+.

## Estrutura

- `data/local`: Room, entidade e DAO da lista local.
- `data/remote`: API Retrofit e DTOs TMDB.
- `data/MovieRepository.kt`: acesso à fonte local e remota.
- `di/AppModule.kt`: dependências Koin.
- `presentation`: estado com StateFlow, telas Compose e navegação.
- `ui/theme`: tema Material 3.

A lista pessoal é armazenada no banco `want-to-watch.db`. Detalhes básicos são apresentados para os resultados de catálogo; elenco, gêneros e duração poderão ser preenchidos pelo endpoint de detalhes do TMDB numa evolução seguinte.
