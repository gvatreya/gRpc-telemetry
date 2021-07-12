
.Phony: install-api
install-api:
	cd api && ${MAKE} install

.Phony: start-server
start-server:
	cd server && ${MAKE} install && ${MAKE} start

.Phony: start-client
start-client:
	cd clients/java && ${MAKE} && ${MAKE} start
