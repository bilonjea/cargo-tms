-- Supprimer la base cargo si elle existe
DROP DATABASE IF EXISTS cargo;

-- Créer la base cargo
CREATE DATABASE cargo;

-- Se connecter à la base cargo (dans psql, pas dans le script SQL pur)
\c cargo;

-- Créer le schéma tms si besoin
CREATE SCHEMA IF NOT EXISTS tms AUTHORIZATION postgres;

-- Créer l’utilisateur postgres (si inexistant déjà, sinon erreur)
DO
$$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'postgres') THEN
      CREATE ROLE postgres LOGIN PASSWORD 'postgres';
   END IF;
END
$$;

-- Donner tous les droits sur la base cargo à l’utilisateur postgres
GRANT ALL PRIVILEGES ON DATABASE cargo TO postgres;

-- Donner tous les droits sur le schéma tms
GRANT ALL PRIVILEGES ON SCHEMA tms TO postgres;
