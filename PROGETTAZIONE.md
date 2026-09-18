# Progetto Unità 5 Settimana 2 — BaldChat

## Entità

### User
- `id`: UUID, PK
- `username`: string, unique
- `email`: string, unique
- `password`: string (hashata Bcrypt)
- `created_at`: Instant

### Chat
- `id`: UUID, PK
- `user1_id`: UUID, FK → User
- `user2_id`: UUID, FK → User
- `created_at`: Instant
- Vincolo di unicità su `(user1_id, user2_id)`

### Messaggio
- `id`: UUID, PK
- `testo`: String
- `created_at`: Instant
- `chat_id`: FK 1:N → Chat
- `mittente_id`: FK 1:N → User

---

## Flussi applicativi

### Apertura di una chat

Un utente vuole iniziare una chat con un altro utente:
- Verifica del vincolo di unicità tra i due utenti (implementata con un OR)
- Creazione della chat se non presente già nel DB, altrimenti apertura della chat esistente
- Apertura della chat e caricamento degli ultimi N messaggi presenti nel DB

Note:
1. Va benissimo con un OR
2. Si passa anche l'id dell'utente che richiede l'apertura della chat (che, se hai implementato JWT, si recupera da lì)
3. Ordine DESC in base al campo `created_at`; N lo puoi gestire lato server con una costante, o lato appsettings (lo setti lì e lo richiami: si può cambiare facilmente senza andare a ricercare ogni volta il servizio)

### Invio di un messaggio

Quando un utente scrive un messaggio nella chat aperta:
- Il messaggio parte
- Controllo dell'ID di chi scrive per verifica di coincidenza con uno dei due id presenti nella chat
- Il messaggio viene salvato nel record relativo a quella chat
- Il messaggio appena salvato viaggia all'altro utente

### Suggerimento AI

L'utente chiede un suggerimento alla AI:
- L'AI analizza gli ultimi N messaggi presenti nel DB di quel record
- Invia i messaggi all'LLM
- L'LLM formula il suggerimento
- Invia il suggerimento direttamente nell'area di testo dell'utente (per eventuale modifica)

### Statistiche utente

Utente chiede le sue statistiche:
- Controllo dei messaggi inviati relativi a quell'id utente
- Controllo dei messaggi ricevuti relativi a quell'id utente
- Controllo delle chat in cui compare come user presente
- Inserimento dei parametri nel template Thymeleaf dell'email
- Invio via email all'utente con Spring Mail

---

## Controller

### Auth

`@RequestMapping("/api/auth")`

**POST `register`** (username, email, password)
Service — controllo su username già usato, email già usata. Crea il nuovo utente.

**POST `login`** (username, password)
Service — verifica errori di validazione, al login invia il token di risposta.

---

## Frontend

Integrare un front-end che abbia senso per questa applicazione, rifacendosi allo stile **React Bootstrap Soft Aurora**.

Il sito si chiama **BaldChat**.
