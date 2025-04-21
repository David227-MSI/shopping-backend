-- remove database ---------------------------------------------------------------------------
USE master;
IF DB_ID('ProjectDB') IS NOT NULL
BEGIN
    ALTER DATABASE ProjectDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE ProjectDB;
    PRINT 'Database ProjectDB dropped successfully';
END