package Registrar;

import CateringFacility.CateringFacility;
import MatchingService.MatchingService;
import User.User;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface Registrar extends Remote {
    LocalDate getDate() throws RemoteException;
    void nextDay() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeySpecException, BadPaddingException, InvalidAlgorithmParameterException;
    //methodes voor user
    void enrollUser(User user) throws RemoteException, NoSuchAlgorithmException, SignatureException, InvalidKeyException;
    PublicKey getPublicKey() throws RemoteException;

    void setMatchingService(MatchingService ms) throws RemoteException;

    /*
    Catering methodes
     */
    String[] checkAuthenticityCatering(long phoneNumber, long businessNumber, String location, String facilityName) throws RemoteException;

    boolean userUniquePhoneNumber(String phoneNumber) throws RemoteException;

    //methodes voor catering
    void enrollCatering(CateringFacility cf) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidKeySpecException;

    void disconnectCatering(CateringFacility cf) throws RemoteException;

    void disconnectUser(User u) throws RemoteException;

    byte[] calculateDailyPseudonym(long CF, String location) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException;

    void tokenInformed(String userToken) throws RemoteException;

    void warnCatering(String cateringBN, String timeFrom, String timeUntil) throws RemoteException;
}
