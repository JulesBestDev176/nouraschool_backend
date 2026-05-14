const express = require('express');
const qrcode = require('qrcode-terminal');
const { Client, LocalAuth } = require('whatsapp-web.js');

const port = Number(process.env.PORT || 3100);
const apiKey = process.env.WHATSAPP_GATEWAY_API_KEY || '';
const clientId = process.env.WHATSAPP_CLIENT_ID || 'nouraschool';

let ready = false;
let lastQr = null;

const app = express();
app.use(express.json({ limit: '64kb' }));

function requireApiKey(req, res, next) {
  if (!apiKey) {
    return res.status(503).json({ error: 'WHATSAPP_GATEWAY_API_KEY is not configured' });
  }
  if (req.header('X-API-Key') !== apiKey) {
    return res.status(401).json({ error: 'Unauthorized' });
  }
  return next();
}

function normalizePhone(phone) {
  const value = String(phone || '').replace(/[^\d+]/g, '');
  if (!value) return '';
  return value.startsWith('+') ? value.slice(1) : value;
}

const client = new Client({
  authStrategy: new LocalAuth({ clientId }),
  puppeteer: {
    headless: true,
    args: [
      '--no-sandbox',
      '--disable-setuid-sandbox',
      '--disable-dev-shm-usage',
      '--disable-gpu'
    ]
  }
});

client.on('qr', (qr) => {
  ready = false;
  lastQr = qr;
  console.log('WhatsApp QR received. Scan it with the WhatsApp mobile app.');
  qrcode.generate(qr, { small: true });
});

client.on('ready', () => {
  ready = true;
  lastQr = null;
  console.log('WhatsApp client is ready.');
});

client.on('authenticated', () => {
  console.log('WhatsApp client authenticated.');
});

client.on('auth_failure', (message) => {
  ready = false;
  console.error('WhatsApp authentication failure:', message);
});

client.on('disconnected', (reason) => {
  ready = false;
  console.warn('WhatsApp client disconnected:', reason);
});

app.get('/health', (_req, res) => {
  res.json({ status: 'UP', ready });
});

app.get('/status', requireApiKey, (_req, res) => {
  res.json({ ready, hasQr: Boolean(lastQr), clientId });
});

app.get('/qr', requireApiKey, (_req, res) => {
  if (!lastQr) {
    return res.status(404).json({ error: 'No QR available' });
  }
  return res.json({ qr: lastQr });
});

app.post('/send', requireApiKey, async (req, res) => {
  if (!ready) {
    return res.status(503).json({ error: 'WhatsApp client is not ready' });
  }

  const to = normalizePhone(req.body && req.body.to);
  const message = String((req.body && req.body.message) || '').trim();
  if (!to || !message) {
    return res.status(400).json({ error: 'to and message are required' });
  }

  try {
    const chatId = `${to}@c.us`;
    const response = await client.sendMessage(chatId, message);
    return res.status(202).json({ id: response.id && response.id._serialized });
  } catch (error) {
    console.error('Unable to send WhatsApp message:', error);
    return res.status(502).json({ error: 'Unable to send WhatsApp message' });
  }
});

client.initialize();

app.listen(port, () => {
  console.log(`WhatsApp gateway listening on ${port}`);
});
