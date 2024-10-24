FROM mobiledevops/android-sdk-image:34.0.0

ARG NEW_RELIC_MOBILE_KEY=''

USER root
WORKDIR /build
COPY ./app/ /build/app
COPY ./deployment/ /build/deployment
COPY ./gradle/ /build/gradle
COPY ./gradlew /build/gradlew
COPY ./build.gradle.kts /build/build.gradle.kts
COPY ./settings.gradle.kts /build/settings.gradle.kts
COPY ./gradle.properties /build/gradle.properties
RUN  cd /build/ && \
     sed -i 's|NEW_RELIC_MOBILE_KEY|'${NEW_RELIC_MOBILE_KEY}'|' app/newrelic.properties && \
     sed -i 's|NEW_RELIC_MOBILE_KEY|'${NEW_RELIC_MOBILE_KEY}'|' app/src/main/java/technology/nrkk/apps/socks/LoginActivity.kt && \
     cat app/newrelic.properties && \
     ./gradlew clean assembleRelease

FROM appium/appium:v2.11.4-p2
COPY --from=0 /build/app/build/outputs/apk/release/app-release.apk /app/app.apk

COPY ./scripts/health.sh /home/androidusr/health.sh
USER root
RUN chmod +x /home/androidusr/health.sh && chown 1300:1301 /home/androidusr/health.sh

USER 1300:1301
CMD adb connect ssh:5555 && ./${SCRIPT_PATH}/start.sh

