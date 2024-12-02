const MediaQuery = {
  //s3 se/6/7/8
  small: "@media (max-width: 376px) and (max-height: 740px)",
  //360 x 740 x s8+ s8
  medium:
    "@media (max-width:376px) and (min-height:740px) and (max-height: 780px)",
  //그 외의 크기는 디폴트 값으로 들어가짐
} as const;

export default MediaQuery;

/**
 *
 * 375 x 812 ok
 * 360 x 740 x s8+
 * 360 x 640 x s3
 * 375 x 667 x se/6/7/8
 *
 */
