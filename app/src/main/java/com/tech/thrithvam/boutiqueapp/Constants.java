package com.tech.thrithvam.boutiqueapp;


public class Constants {
    String AppID="ADB68305-64C5-4181-830F-43BA6210B226";  //Find way to generate this
    //Define which is the boutique
    String BoutiqueID= "470A044A-4DBA-4770-BCA7-331D2C0834AE";//tr
            // "e4ce4213-b1dc-443f-8576-4778f35e7383";

            //"e4ce4213-b1dc-443f-8576-4778f35e7383";//suv
            //
    //"4362cb98-dfb3-4533-ad1b-6caecc0ef5a1";//i-boutiq
    String BoutiqueName="Thrithvam";
    int MobileNumberMax=10;
    int MobileNumberMin=10;
    String MobileNumberRegularExpression = "^[0-9]*$";
    String UserNameRegularExpression="^[a-zA-Z\\. ]+$";                 //^[a-z0-9_-]{3,15}$
    int UserNameMin=3;
    int productsCountLimit=4;           //0 For no limits
    int relatedProductsCountLimit=0;    //0 For no limits
    int newArrivalsCountLimit=0;        //0 For no limits
}
