SHOPCO BACKEND

OVERVIEW

Shopco is a modular, scalable e-commerce backend built with Spring Boot 3.5, Java 21, and PostgreSQL, designed following clean architecture.
It supports:

1. Product view and search ( Customer-facing browsing)
2. Authentication =>> JWT Authentication ( Access token and Refresh token logic)
3. Cart system
4. Payments via Monnify api
5. Order management
6. File upload to Cloudinary
7. Product + Variants management by Admin
8. Full Dockerization
9. FlyWay Database Migration
10. Adding members as an admin
11. Email Service via Zeptomail by Zoho


Shopco powers the complete checkout workflow:

Product → Cart → Payment Initiation → Payment Verification → Order Creation -> Order Completion


FIGMA DESIGN : 

Note: The design does not cover Authentication and Order Interface

Front engineer should Improvise


UI/UX for this project is designed in Figma: 
https://www.figma.com/design/MwTVYg6OdQc5iXfpT8sJpS/E-commerce-Website-Template--Freebie---Community-?node-id=1-2

DOCUMENTATION :

The application is deployed on render. The documentation link -->> 
https://shopco-5u4w.onrender.com/swagger-ui/index.html


HOW TO DEPLOY THE APP ON YOUR OWN SERVER

The app is already dockerized so deployment is stressless

1. Create a .env file by following the .env.example

2. Get your credentials and fill in the values 

3. The value of SPRING_PROFILES_ACTIVE is your environment. e.g If you are working with test environment i.e application-test.yml. the value of this variable will be test


