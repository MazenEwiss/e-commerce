import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class KeyGen {
    public static void main(String[] args) throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair pair = gen.generateKeyPair();

        System.out.println("PRIVATE KEY (wallet-service only):");
        System.out.println(Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded()));
        System.out.println();
        System.out.println("PUBLIC KEY (all three services):");
        System.out.println(Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()));
    }
}