ssl/localhost.p12:
	mkdir ssl && cd ssl && mkcert -install -pkcs12 localhost

ssl/keystore.jks: ssl/localhost.p12
	keytool -importkeystore -destkeystore ssl/keystore.jks -srcstoretype PKCS12 -srckeystore ssl/localhost.p12 --srcstorepass changeit -deststorepass changeit
