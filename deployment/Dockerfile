FROM amazon/aws-cli:2.18.9

COPY ./ssh/config /root/.ssh/config
COPY ./scripts/init.sh /root/init.sh
COPY ./scripts/health.sh /root/health.sh
RUN yum -y update && yum -y install openssh-clients && chmod +x /root/*.sh

ENV GENY_IP_TARGET=PrivateIpAddress
ENV AWS_ACCESS_KEY_ID=AWS_ACCESS_KEY_ID
ENV AWS_SECRET_ACCESS_KEY=AWS_SECRET_ACCESS_KEY
ENV AWS_REGION=AWS_REGION

ENTRYPOINT /root/init.sh
