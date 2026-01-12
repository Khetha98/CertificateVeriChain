a) Build transaction

cliService.buildTransaction(utxoHash, utxoIndex, ISSUER_ADDRESS, metadata)

Make sure it returns a valid .tx file path.

Check that the CLI command syntax matches current cardano-cli version.

Typical Cardano build command:

cardano-cli transaction build \
--alonzo-era \
--tx-in <TX_HASH>#<TX_INDEX> \
--tx-out <ADDRESS>+0 \
--metadata-json-file <METADATA_FILE> \
--testnet-magic 1 \
--out-file tx.raw

\\works
cardano-cli conway transaction build \
--testnet-magic 1 \
--tx-in e64b692d4e89f13f83484fa3fe373793f064713d4f67fa2b66fbffa1e6f71bd9#0 \
--tx-out $(cat issuer.addr)+3000000 \
--change-address $(cat issuer.addr) \
--metadata-json-file metadata.json \
--out-file tx.raw
Estimated transaction fee: 175929 Lovelace


b) Sign transaction
cardano-cli transaction sign \
--tx-body-file tx.raw \
--signing-key-file issuer.skey \
--testnet-magic 1 \
--out-file tx.signed

\\works
cardano-cli conway transaction sign \
--tx-body-file tx.raw \
--signing-key-file issuer.skey \
--testnet-magic 1 \
--out-file tx.signed


c) Submit transaction
cardano-cli transaction submit \
--tx-file tx.signed \
--testnet-magic 1

\\works
cardano-cli conway transaction submit \
--tx-file tx.signed \
--testnet-magic 1

\\check if have enough tokens commamd
cardano-cli query utxo --address $(cat issuer.addr) --testnet-magic 1


\\run project via terminal
mvn spring-boot:run