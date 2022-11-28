package Registrar;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;

public class RunRegistrar {
    public static void main(String[] args) throws NoSuchAlgorithmException, RemoteException, AlreadyBoundException {
        Registrar registrar = new RegistrarImpl();
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.bind("Registrar", registrar);
        System.out.println("Registrar started");
    }
}