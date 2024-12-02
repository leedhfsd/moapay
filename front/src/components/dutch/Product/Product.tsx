import { ProductCard, ProductInfo } from "./Product.styles";

import testImg from "./../../../assets/image/image.png";

interface ProductProps {
  productName: string;
  productUrl: string;
  productImg: string;
}

const Product = ({ productName, productUrl, productImg }: ProductProps) => {
  return (
    <ProductCard
      // onClick={() => {
      //   window.open(productUrl);
      // }}
    >
      {/* 상품 미리보기 사진 */}
      <div>
        <img src={productImg} />
      </div>

      <ProductInfo>
        {/* 상품명 */}
        <div>{productName}</div>

        {/* 상품 url */}
        {/* <div>{productUrl}</div> */}
      </ProductInfo>
    </ProductCard>
  );
};

export default Product;
