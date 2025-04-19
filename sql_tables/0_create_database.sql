-- create database ---------------------------------------------------------------------------
USE master;
IF DB_ID('ProjectDB') IS NULL
    CREATE DATABASE ProjectDB COLLATE Chinese_Taiwan_Stroke_CI_AS;
ELSE
    SELECT 'ProjectDB already exists'

USE ProjectDB;