#!/bin/bash

# Variables
CONTAINER_NAME="c9104cb26e8a"   # Replace with your container name
DB_NAME="bnvregistrudb"         # Replace with your database name
USER="postgres"                 # Replace with your database user
BACKUP_DIR="/mnt/e/Facultate/Project/Backup" # Replace with your backup directory in WSL format
BACKUP_FILE="$BACKUP_DIR/backup_$(date +%Y-%m-%d).sql"

# Create the backup directory if it doesn't exist
mkdir -p "$BACKUP_DIR"

# Run pg_dump inside the container and save the backup to the host
docker exec -t "$CONTAINER_NAME" pg_dump -U "$USER" -d "$DB_NAME" > "$BACKUP_FILE"

# Log the backup operation
echo "Backup created: $BACKUP_FILE"
