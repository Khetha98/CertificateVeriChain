package za.co.certificateVeriChain.certificateVeriChainBackend.helpers;

import com.bloxbean.cardano.client.function.TxBuilderContext;
import com.bloxbean.cardano.client.transaction.spec.Transaction;

public record TxBuildResult(Transaction tx, TxBuilderContext context) {}


