package zkp.utilities;

import cyclops.collections.immutable.VectorX;
import edu.stanford.cs.crypto.efficientct.VerificationFailedException;
import edu.stanford.cs.crypto.efficientct.algebra.BN128Group;
import edu.stanford.cs.crypto.efficientct.algebra.BN128Point;
import edu.stanford.cs.crypto.efficientct.burnprover.*;
import edu.stanford.cs.crypto.efficientct.linearalgebra.GeneratorVector;
import edu.stanford.cs.crypto.efficientct.zetherprover.*;

import java.math.BigInteger;

public class Verifier {

    private BN128Group group = Params.getGroup();
    private ZetherVerifier<BN128Point> zetherVerifier = new ZetherVerifier<>();
    private BurnVerifier<BN128Point> burnVerifier = new BurnVerifier<>();

    public boolean verifyTransfer(byte[][] CLnBytes, byte[][] CRnBytes, byte[][] LBytes, byte[] RBytes, byte[][] yBytes, byte[] epoch, byte[] u, byte[] proof) {
        // note note note note note: CL and CR now reflect _pre_-deduction states. this is purely a design choice, it's easier to do it this way this time because
        // unlike before, acc - outL no longer needs to be calculated within-contract, either for the sender or for anyone else.
        int size = yBytes.length;
        GeneratorVector<BN128Point> y = GeneratorVector.from(VectorX.range(0, size).map(i -> BN128Point.unserialize(yBytes[i])), group);
        GeneratorVector<BN128Point> L = GeneratorVector.from(VectorX.range(0, size).map(i -> BN128Point.unserialize(LBytes[i])), group);
        BN128Point R = BN128Point.unserialize(RBytes);
//        GeneratorVector<BN128Point> CLn = L.add(GeneratorVector.from(VectorX.range(0, size).map(i -> BN128Point.unserialize(CLBytes[i])), group));
//        GeneratorVector<BN128Point> CRn = GeneratorVector.from(VectorX.range(0, size).map(i -> BN128Point.unserialize(CRBytes[i]).add(R)), group);
        GeneratorVector<BN128Point> CLn = GeneratorVector.from(VectorX.range(0, size).map(i -> BN128Point.unserialize(CLnBytes[i])), group);
        GeneratorVector<BN128Point> CRn = GeneratorVector.from(VectorX.range(0, size).map(i -> BN128Point.unserialize(CRnBytes[i])), group);

        ZetherStatement<BN128Point> zetherStatement = new ZetherStatement<>(CLn, CRn, L, R, y, new BigInteger(1, epoch).intValue(), BN128Point.unserialize(u));
        ZetherProof<BN128Point> zetherProof = ZetherProof.unserialize(proof);
        boolean success = true;
        System.out.println("CLn: " + CLn);
        System.out.println("CRn: " + CRn);
        try {
            zetherVerifier.verify(Params.getZetherParams(), zetherStatement, zetherProof);
        } catch (VerificationFailedException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
//        byte[] arr = new byte[32];
//        if (success)
//            arr[0] = 1;
//        return arr;
    }

    public boolean verifyBurn(byte[] CLn, byte[] CRn, byte[] y, byte[] bTransfer, byte[] epoch, byte[] u, byte[] proof) {
        BurnStatement<BN128Point> burnStatement = new BurnStatement<>(BN128Point.unserialize(CLn), BN128Point.unserialize(CRn), BN128Point.unserialize(y), new BigInteger(1, bTransfer), new BigInteger(1, epoch).intValue(), BN128Point.unserialize(u));
        BurnProof<BN128Point> burnProof = BurnProof.unserialize(proof);
        boolean success = true;
        try {
            burnVerifier.verify(Params.getBurnParams(), burnStatement, burnProof);
        } catch (VerificationFailedException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
//        byte[] arr = new byte[32];
//        if (success)
//            arr[0] = 1;
//        return arr;
    }
}