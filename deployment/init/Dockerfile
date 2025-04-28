FROM amazon/aws-cli:2.18.9

COPY ./ssh/config /root/.ssh/config
COPY ./scripts/init.sh /root/init.sh
RUN yum -y update && yum -y install openssh-clients && chmod +x /root/*.sh

ENV GENY_IP_TARGET=PrivateIpAddress
ENTRYPOINT /root/init.sh

