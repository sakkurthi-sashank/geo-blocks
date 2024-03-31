// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC721/ERC721.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/utils/Counters.sol";

contract YourContract is ERC721, Ownable {
    using Counters for Counters.Counter;
    Counters.Counter private _tokenIdCounter;

    event TokenURIUpdated(uint256 indexed tokenId, string tokenURI);
    event NFTBought(uint256 indexed tokenId, address buyer);
    event NFTSold(uint256 indexed tokenId, address seller, address buyer, uint256 price);
    event NFTListed(uint256 indexed tokenId, address seller, uint256 price);

    mapping(uint256 => bool) private _tokenExists;
    mapping(uint256 => uint256) private _tokenPrices;
    mapping(uint256 => string) private _tokenTitles;
    mapping(uint256 => string) private _tokenDescriptions;
    mapping(uint256 => string) private _tokenImageURLs;

    constructor() ERC721("MyNFT", "MNFT") {}

    function mintNFT(string memory _title, string memory _description, string memory _imageURL, address _to) public onlyOwner returns (uint256) {
        _tokenIdCounter.increment();
        uint256 newTokenId = _tokenIdCounter.current();

        _safeMint(_to, newTokenId);
        emit TokenURIUpdated(newTokenId, _imageURL);

        _tokenExists[newTokenId] = true;
        _tokenTitles[newTokenId] = _title;
        _tokenDescriptions[newTokenId] = _description;
        _tokenImageURLs[newTokenId] = _imageURL;

        return newTokenId;
    }

    modifier tokenExists(uint256 _tokenId) {
        require(_tokenExists[_tokenId], "Token does not exist");
        _;
    }

    function buyNFT(uint256 _tokenId) public payable tokenExists(_tokenId) {
        require(msg.value >= _tokenPrices[_tokenId], "Insufficient funds");

        address seller = ownerOf(_tokenId);
        address buyer = msg.sender;

        _transfer(seller, buyer, _tokenId);
        emit NFTBought(_tokenId, buyer);

        if (msg.value > _tokenPrices[_tokenId]) {
            payable(buyer).transfer(msg.value - _tokenPrices[_tokenId]);
        }

        delete _tokenPrices[_tokenId];
    }

    function sellNFT(uint256 _tokenId, uint256 _price) public tokenExists(_tokenId) {
        require(ownerOf(_tokenId) == msg.sender, "You are not the owner of this token");

        _tokenPrices[_tokenId] = _price;

        emit NFTListed(_tokenId, msg.sender, _price);
    }

    function displayNFT(uint256 _tokenId) public view tokenExists(_tokenId) returns (string memory) {
        return _tokenImageURLs[_tokenId];
    }

    function airdropNFT(address _to, string memory _title, string memory _description, string memory _imageURL) public onlyOwner returns (uint256) {
        _tokenIdCounter.increment();
        uint256 newTokenId = _tokenIdCounter.current();

        _safeMint(_to, newTokenId);
        emit TokenURIUpdated(newTokenId, _imageURL);

        _tokenExists[newTokenId] = true;
        _tokenTitles[newTokenId] = _title;
        _tokenDescriptions[newTokenId] = _description;
        _tokenImageURLs[newTokenId] = _imageURL;

        return newTokenId;
    }

    function listAllNFTs() public view returns (NFT[] memory) {
        NFT[] memory nfts = new NFT[](_tokenIdCounter.current());
        uint256 index = 0;
        for (uint256 i = 1; i <= _tokenIdCounter.current(); i++) {
            if (_tokenExists[i]) {
                nfts[index] = NFT({
                    tokenId: i,
                    title: _tokenTitles[i],
                    description: _tokenDescriptions[i],
                    imageURL: _tokenImageURLs[i]
                });
                index++;
            }
        }
        return nfts;
    }

    struct NFT {
        uint256 tokenId;
        string title;
        string description;
        string imageURL;
    }
}
