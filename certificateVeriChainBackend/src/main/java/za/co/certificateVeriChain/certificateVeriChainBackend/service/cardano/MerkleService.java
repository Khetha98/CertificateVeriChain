package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MerkleService {

    private final ObjectMapper mapper = new ObjectMapper();

    // Build Merkle Tree and return proof map
    public Map<String, List<String>> generateProofs(List<String> hashes) {

        List<String> sorted = hashes.stream().sorted().toList();

        List<List<String>> tree = new ArrayList<>();
        tree.add(sorted);

        while (tree.get(tree.size() - 1).size() > 1) {
            List<String> level = tree.get(tree.size() - 1);
            List<String> nextLevel = new ArrayList<>();

            for (int i = 0; i < level.size(); i += 2) {
                String left = level.get(i);
                String right = (i + 1 < level.size()) ? level.get(i + 1) : left;

                nextLevel.add(hashPair(left, right));
            }

            tree.add(nextLevel);
        }

        Map<String, List<String>> proofs = new HashMap<>();

        for (int i = 0; i < sorted.size(); i++) {
            List<String> proof = new ArrayList<>();
            int index = i;

            for (int level = 0; level < tree.size() - 1; level++) {
                List<String> nodes = tree.get(level);

                int siblingIndex = (index % 2 == 0) ? index + 1 : index - 1;

                if (siblingIndex < nodes.size()) {
                    proof.add(nodes.get(siblingIndex));
                }

                index /= 2;
            }

            proofs.put(sorted.get(i), proof);
        }

        return proofs;
    }

    public String calculateRoot(List<String> hashes) {
        List<String> level = new ArrayList<>(hashes);

        while (level.size() > 1) {
            List<String> next = new ArrayList<>();

            for (int i = 0; i < level.size(); i += 2) {
                String left = level.get(i);
                String right = (i + 1 < level.size()) ? level.get(i + 1) : left;

                next.add(hashPair(left, right));
            }

            level = next;
        }

        return level.get(0);
    }

    public boolean verify(String certHash, String merkleProofJson, String merkleRoot) {
        try {
            List<String> proof = mapper.readValue(
                    merkleProofJson,
                    mapper.getTypeFactory().constructCollectionType(List.class, String.class)
            );

            String hash = certHash;

            for (String sibling : proof) {
                hash = hashPair(hash, sibling);
            }

            return hash.equals(merkleRoot);

        } catch (Exception e) {
            throw new RuntimeException("Invalid merkle proof format", e);
        }
    }

    private String hashPair(String a, String b) {
        return DigestUtils.sha256Hex(a.compareTo(b) < 0 ? a + b : b + a);
    }

    public String toJson(List<String> proof) {
        try {
            return mapper.writeValueAsString(proof);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize proof", e);
        }
    }
}

