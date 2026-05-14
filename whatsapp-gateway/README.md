# NouraSchool WhatsApp Gateway

Petit service Node.js qui expose une API interne pour envoyer les OTP WhatsApp avec `whatsapp-web.js`.

## Variables

- `PORT` : port HTTP, défaut `3100`
- `WHATSAPP_GATEWAY_API_KEY` : clé requise dans le header `X-API-Key`
- `WHATSAPP_CLIENT_ID` : nom de session WhatsApp, défaut `nouraschool`

## Démarrage local

```bash
npm install
npm start
```

Au premier démarrage, scanner le QR code affiché dans les logs avec WhatsApp mobile. La session est sauvegardée par `LocalAuth`.

## Endpoints

- `GET /health` : santé technique
- `GET /status` : état WhatsApp, protégé par `X-API-Key`
- `GET /qr` : QR courant si disponible, protégé par `X-API-Key`
- `POST /send` : envoi message, protégé par `X-API-Key`

Payload `POST /send` :

```json
{
  "to": "+221770000000",
  "message": "Votre code NouraSchool est : 123456"
}
```

## Note production

`whatsapp-web.js` est une librairie non officielle basée sur WhatsApp Web. Prévoir un numéro dédié, une session persistante et une supervision du statut `/status`.
