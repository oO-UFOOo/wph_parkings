# Nginx HTTPS Setup for SOPA OpenEye

## Goal
Serve the frontend over HTTPS in your VM while proxying API requests to the Spring Boot app.

## Recommended architecture
- Nginx terminates HTTPS on port 443
- Nginx serves `index.html` and `dashboard.html`
- Nginx proxies `/api/*` to Spring Boot on `http://127.0.0.1:8080`

## Example Nginx config

```nginx
server {
    listen 80;
    server_name sopa-openeye-security.gr www.sopa-openeye-security.gr;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2;
    server_name sopa-openeye-security.gr www.sopa-openeye-security.gr;

    ssl_certificate     /etc/letsencrypt/live/sopa-openeye-security.gr/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/sopa-openeye-security.gr/privkey.pem;

    root /var/www/sopa-backend/src/main/resources/static;
    index index.html;

    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto https;
        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Forwarded-Port 443;
    }

    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

## Steps on the VM
1. Pull the latest code.
2. Rebuild/restart Spring Boot.
3. Reload Nginx.
4. Open `https://sopa-openeye-security.gr`.

## Verify
- `https://sopa-openeye-security.gr/` → custom SOPA login page
- `https://sopa-openeye-security.gr/api/users` → proxied to Spring Boot

```

