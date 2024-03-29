import { BigInt } from "@graphprotocol/graph-ts"
import {
  NFTMinter,
  Approval,
  ApprovalForAll,
  BatchMetadataUpdate,
  DefaultRoyalty,
  EIP712DomainChanged,
  FlatPlatformFeeUpdated,
  Initialized,
  MetadataFrozen,
  MetadataUpdate,
  OwnerUpdated,
  PlatformFeeInfoUpdated,
  PlatformFeeTypeUpdated,
  PrimarySaleRecipientUpdated,
  RoleAdminChanged,
  RoleGranted,
  RoleRevoked,
  RoyaltyForToken,
  TokensMinted,
  TokensMintedWithSignature,
  Transfer
} from "../generated/NFTMinter/NFTMinter"
import { ExampleEntity } from "../generated/schema"

export function handleApproval(event: Approval): void {
  // Entities can be loaded from the store using a string ID; this ID
  // needs to be unique across all entities of the same type
  let entity = ExampleEntity.load(event.transaction.from)

  // Entities only exist after they have been saved to the store;
  // `null` checks allow to create entities on demand
  if (!entity) {
    entity = new ExampleEntity(event.transaction.from)

    // Entity fields can be set using simple assignments
    entity.count = BigInt.fromI32(0)
  }

  // BigInt and BigDecimal math are supported
  entity.count = entity.count + BigInt.fromI32(1)

  // Entity fields can be set based on event parameters
  entity.owner = event.params.owner
  entity.approved = event.params.approved

  // Entities can be written to the store with `.save()`
  entity.save()

  // Note: If a handler doesn't require existing field values, it is faster
  // _not_ to load the entity from the store. Instead, create it fresh with
  // `new Entity(...)`, set the fields that should be updated and save the
  // entity back to the store. Fields that were not set or unset remain
  // unchanged, allowing for partial updates to be applied.

  // It is also possible to access smart contracts from mappings. For
  // example, the contract that has emitted the event can be connected to
  // with:
  //
  // let contract = Contract.bind(event.address)
  //
  // The following functions can then be called on this contract to access
  // state variables and other data:
  //
  // - contract.DEFAULT_ADMIN_ROLE(...)
  // - contract.balanceOf(...)
  // - contract.contractType(...)
  // - contract.contractURI(...)
  // - contract.contractVersion(...)
  // - contract.eip712Domain(...)
  // - contract.getApproved(...)
  // - contract.getDefaultRoyaltyInfo(...)
  // - contract.getPlatformFeeInfo(...)
  // - contract.getRoleAdmin(...)
  // - contract.getRoleMember(...)
  // - contract.getRoleMemberCount(...)
  // - contract.getRoyaltyInfoForToken(...)
  // - contract.hasRole(...)
  // - contract.isApprovedForAll(...)
  // - contract.isTrustedForwarder(...)
  // - contract.mintTo(...)
  // - contract.multicall(...)
  // - contract.name(...)
  // - contract.nextTokenIdToMint(...)
  // - contract.owner(...)
  // - contract.ownerOf(...)
  // - contract.platformFeeRecipient(...)
  // - contract.primarySaleRecipient(...)
  // - contract.royaltyInfo(...)
  // - contract.supportsInterface(...)
  // - contract.symbol(...)
  // - contract.tokenByIndex(...)
  // - contract.tokenOfOwnerByIndex(...)
  // - contract.tokenURI(...)
  // - contract.totalSupply(...)
  // - contract.uriFrozen(...)
  // - contract.verify(...)
}

export function handleApprovalForAll(event: ApprovalForAll): void {}

export function handleBatchMetadataUpdate(event: BatchMetadataUpdate): void {}

export function handleDefaultRoyalty(event: DefaultRoyalty): void {}

export function handleEIP712DomainChanged(event: EIP712DomainChanged): void {}

export function handleFlatPlatformFeeUpdated(
  event: FlatPlatformFeeUpdated
): void {}

export function handleInitialized(event: Initialized): void {}

export function handleMetadataFrozen(event: MetadataFrozen): void {}

export function handleMetadataUpdate(event: MetadataUpdate): void {}

export function handleOwnerUpdated(event: OwnerUpdated): void {}

export function handlePlatformFeeInfoUpdated(
  event: PlatformFeeInfoUpdated
): void {}

export function handlePlatformFeeTypeUpdated(
  event: PlatformFeeTypeUpdated
): void {}

export function handlePrimarySaleRecipientUpdated(
  event: PrimarySaleRecipientUpdated
): void {}

export function handleRoleAdminChanged(event: RoleAdminChanged): void {}

export function handleRoleGranted(event: RoleGranted): void {}

export function handleRoleRevoked(event: RoleRevoked): void {}

export function handleRoyaltyForToken(event: RoyaltyForToken): void {}

export function handleTokensMinted(event: TokensMinted): void {}

export function handleTokensMintedWithSignature(
  event: TokensMintedWithSignature
): void {}

export function handleTransfer(event: Transfer): void {}
