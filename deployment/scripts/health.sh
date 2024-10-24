#/bin/bash
export GENY_IP=$(aws ec2 describe-instances --output=text --filters Name=instance-state-name,Values=running --query 'Reservations[].Instances[?Tags[?Key==`Name`].Value|[0]==`Genymotion`][].'$GENY_IP_TARGET)

if [ "shell" = "$(timeout 3 ssh $GENY_IP -- whoami)" ];
then
  echo "working"
  exit 0;
else
  echo "error"
  exit 1;
fi