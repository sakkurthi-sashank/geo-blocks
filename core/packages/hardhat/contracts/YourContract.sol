// SPDX-License-Identifier: MIT
pragma solidity >=0.8.0 <0.9.0;

import "@openzeppelin/contracts/token/ERC721/extensions/ERC721URIStorage.sol";
import "@openzeppelin/contracts/security/ReentrancyGuard.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/utils/Counters.sol";

contract YourContract is ERC721URIStorage, ReentrancyGuard, Ownable {
    using Strings for uint256;
    using Counters for Counters.Counter;

    Counters.Counter private _tokenIdCounter;

    string public baseURI;
    string public baseExtension = ".json";
    uint256 public cost = 0.01 ether;
    uint256 public maxSupply = 10000;
    bool public paused = false;

    // Marketplace variables
    struct Listing {
        uint256 tokenId;
        address payable seller;
        uint256 price;
        bool isSold;
    }

    Listing[] public listings;

    event Listed(
        uint256 indexed tokenId,
        address seller,
        uint256 price
    );

    event Sold(
        uint256 indexed tokenId,
        address buyer,
        uint256 price
    );

    constructor(string memory _name, string memory _symbol, string memory _initBaseURI) ERC721(_name, _symbol) {
        setBaseURI(_initBaseURI);
    }

    // Minting function
    function mint(string memory _tokenURI) public payable returns (uint256) {
        require(!paused, "Minting is paused");
        require(_tokenIdCounter.current() + 1 <= maxSupply, "Max supply exceeded");
        require(msg.value >= cost, "Insufficient funds");

        _tokenIdCounter.increment();
        uint256 newTokenId = _tokenIdCounter.current();

        _safeMint(msg.sender, newTokenId);
        _setTokenURI(newTokenId, _tokenURI);

        return newTokenId;
    }

    // Total Supply function
    function totalSupply() public view returns (uint256) {
        return _tokenIdCounter.current();
    }

    // Listing function
    function listToken(uint256 tokenId, uint256 price) public nonReentrant {
        require(ownerOf(tokenId) == msg.sender, "Not the token owner");
        require(price > 0, "Price must be at least 1 wei");

        transferFrom(msg.sender, address(this), tokenId);

        listings.push(Listing(tokenId, payable(msg.sender), price, false));

        emit Listed(tokenId, msg.sender, price);
    }

    // Buying function
    function buyToken(uint256 listingIndex) public payable nonReentrant {
        Listing storage listing = listings[listingIndex];
        require(!listing.isSold, "Item already sold");
        require(msg.value >= listing.price, "Insufficient funds sent");

        listing.isSold = true;
        _transfer(address(this), msg.sender, listing.tokenId);
        listing.seller.transfer(listing.price);

        emit Sold(listing.tokenId, msg.sender, listing.price);
    }

    // Additional functions
    function setCost(uint256 _newCost) public onlyOwner {
        cost = _newCost;
    }

    function setBaseURI(string memory _newBaseURI) public onlyOwner {
        baseURI = _newBaseURI;
    }

    function setPaused(bool _state) public onlyOwner {
        paused = _state;
    }

    // Utility functions
    function getUnsoldListings() public view returns (Listing[] memory) {
        uint256 unsoldCount = 0;
        for (uint256 i = 0; i < listings.length; i++) {
            if (!listings[i].isSold) {
                unsoldCount++;
            }
        }

        Listing[] memory unsoldListings = new Listing[](unsoldCount);
        uint256 currentIndex = 0;
        for (uint256 i = 0; i < listings.length; i++) {
            if (!listings[i].isSold) {
                unsoldListings[currentIndex] = listings[i];
                currentIndex++;
            }
        }

        return unsoldListings;
    }
}
