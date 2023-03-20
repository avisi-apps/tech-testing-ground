NGROK_BIN = $(shell yarn bin ngrok)
LOCAL_ENV_FILES = config/.env.local
LOCAL_SECRET_FILES = config/.secret.local

export GOOGLE_APPLICATION_CREDENTIALS=$(shell pwd)/app-credentials.json

ngrok: yarn.lock
	${NGROK_BIN} http 3000 --bind-tls true --region eu --subdomain=${USER}

dev-firebase: yarn.lock
	yarn firebase emulators:start --import=./target/firebase --export-on-exit --project tech-testing-ground
