all:
	cd frontend; npm run build

deploy_frontend:
	cd frontend; npm run build
	gsutil -m rsync -d -r build/frontend gs://developersam.com
	gsutil -m acl ch -r -u AllUsers:R gs://developersam.com/*

build_backend_as_container_local:
	./gradlew build
	mkdir -p build/staging
	cp build/libs/website-5.0-all.jar build/staging
	cp src/main/docker/* build/staging
	cd build/staging; \
	docker build -t local-test-image .; \
	docker run -p 8080:8080 --name local-test-container local-test-image

build_backend_as_container:
	./gradlew build
	mkdir -p build/staging
	cp build/libs/website-5.0-all.jar build/staging
	cp src/main/docker/* build/staging
	cd build/staging; gcloud config set project dev-sam; \
	gcloud container builds submit --tag gcr.io/dev-sam/backend-container .

update_indices:
	cd ~/.config/gcloud/emulators/datastore/WEB-INF; \
	gcloud datastore create-indexes index.yaml;\
	gcloud datastore cleanup-indexes index.yaml
