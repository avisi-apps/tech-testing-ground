NGROK_BIN = $(shell yarn bin ngrok)

ngrok: yarn.lock
	${NGROK_BIN} http 3002 --bind-tls true --region eu --subdomain=${USER}
