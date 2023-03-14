NGROK_BIN = $(shell yarn bin ngrok)

ngrok: yarn.lock
	${NGROK_BIN} http 5000 --bind-tls true --region eu --subdomain=${USER}
