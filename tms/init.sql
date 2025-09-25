USE master;
GO

-- Attempting to delete the database if it exists
IF EXISTS (SELECT name FROM master.dbo.sysdatabases WHERE name = 'Aml')
    BEGIN
        PRINT 'Dropping existing database Aml...';
        DROP DATABASE Aml;
        PRINT 'Database dropped successfully.';
    END
GO

CREATE DATABASE Aml;
GO

USE Aml;
GO

CREATE LOGIN Aml WITH PASSWORD = 'Aml', CHECK_POLICY = OFF;
CREATE USER Aml FOR LOGIN Aml;
EXEC sp_addrolemember 'db_owner', 'Aml';
GO