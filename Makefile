all:
	cd src/main/frontend; npm run build
	./gradlew build

backend:
	./gradlew build

deploy_backend:
	./gradlew appengineDeploy

deploy:
	cd src/main/frontend; npm run build
	./gradlew appengineDeploy
