package xyz.haofamily.susie.content.oss;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import xyz.haofamily.susie.content.ContentStorageService;
import xyz.haofamily.susie.content.ContentStorageUtil;

/**
 * 
 * <p>
 * OssContentStorageService
 * </p>
 * 
 * @author Hualei Hao
 * @date 2024-05-29
 */
public class OssContentStorageService implements ContentStorageService {

  private static final Logger logger = LoggerFactory.getLogger(OssContentStorageService.class);

  private static final int PART_SIZE = 10485760;

  private OssConfigProperties ossConfigProperties;

  private MinioClient ossClient;

  public OssContentStorageService(OssConfigProperties ossConfigProperties) {
    this.ossConfigProperties = ossConfigProperties;
    this.ossClient = MinioClient.builder()
        .endpoint(ossConfigProperties.getEndpoint())
        .credentials(ossConfigProperties.getCredentials().getAccessKey(),
            ossConfigProperties.getCredentials().getSecretKey())
        .build();
    try {
      logger.debug("Checking if bucket '{}' exists in server '{}'.", ossConfigProperties.getBucketName(),
          ossConfigProperties.getEndpoint());
      if (!this.ossClient
          .bucketExists(BucketExistsArgs.builder().bucket(ossConfigProperties.getBucketName()).build())) {
        logger.debug("Bucket '{}' does not exist, creating it.", ossConfigProperties.getBucketName());
        ossClient.makeBucket(MakeBucketArgs.builder().bucket(ossConfigProperties.getBucketName()).build());
        ossClient.setBucketPolicy(
            SetBucketPolicyArgs.builder().bucket(ossConfigProperties.getBucketName()).config(bucketPolicy()).build());
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize OSS content storage service.", e);
    }
  }

  @Override
  public String store(@NonNull InputStream in) throws IOException {
    try {
      String key = ContentStorageUtil.generateKey();
      this.ossClient.putObject(PutObjectArgs.builder().bucket(this.ossConfigProperties.getBucketName()).object(key)
          .stream(in, -1, PART_SIZE).build());
      return key;
    } catch (InvalidKeyException | InsufficientDataException | IllegalArgumentException e) {
      if (e instanceof IllegalArgumentException) {
        logger.error("Failed to store data to the OSS server.", e.getLocalizedMessage());
        throw (IllegalArgumentException) e;
      } else {
        throw new IllegalArgumentException("Failed to store data to the OSS server.", e);
      }
    } catch (ErrorResponseException | InvalidResponseException | ServerException | XmlParserException e) {
      throw new IOException("Failed to store data to the OSS server.", e);
    } catch (InternalException | NoSuchAlgorithmException e) {
      throw new RuntimeException("Failed to store data to the OSS server.", e);
    }
  }

  @Override
  public void delete(@NonNull String key) throws IOException {
    try {
      this.ossClient
          .removeObject(RemoveObjectArgs.builder().bucket(ossConfigProperties.getBucketName()).object(key).build());
    } catch (InvalidKeyException | InsufficientDataException e) {
      throw new IllegalArgumentException("Failed to fetch data to the OSS server.", e);
    } catch (ErrorResponseException | InvalidResponseException | ServerException | XmlParserException e) {
      throw new IOException("Failed to fetch data to the OSS server.", e);
    } catch (InternalException | NoSuchAlgorithmException e) {
      throw new RuntimeException("Failed to fetch data to the OSS server.", e);
    }
  }

  @Override
  public InputStream fetch(@NonNull String key) throws IOException {
    try {
      return this.ossClient
          .getObject(GetObjectArgs.builder().bucket(ossConfigProperties.getBucketName()).object(key).build());
    } catch (InvalidKeyException | InsufficientDataException e) {
      throw new IllegalArgumentException("Failed to fetch data to the OSS server.", e);
    } catch (ErrorResponseException | InvalidResponseException | ServerException | XmlParserException e) {
      throw new IOException("Failed to fetch data to the OSS server.", e);
    } catch (InternalException | NoSuchAlgorithmException e) {
      throw new RuntimeException("Failed to fetch data to the OSS server.", e);
    }
  }

  @Override
  public long length(@NonNull String key) throws IOException {
    try {
      return this.ossClient
          .statObject(StatObjectArgs.builder().bucket(ossConfigProperties.getBucketName()).object(key).build()).size();
    } catch (InvalidKeyException | InsufficientDataException e) {
      throw new IllegalArgumentException("Failed to fetch data to the OSS server.", e);
    } catch (ErrorResponseException | InvalidResponseException | ServerException | XmlParserException e) {
      throw new IOException("Failed to fetch data to the OSS server.", e);
    } catch (InternalException | NoSuchAlgorithmException e) {
      throw new RuntimeException("Failed to fetch data to the OSS server.", e);
    }
  }

  private String bucketPolicy() {
    ObjectMapper mapper = new ObjectMapper();
    // @formatter:off
    ObjectNode bucketPolicy = mapper.createObjectNode()
      .put("Effect", "Allow")
      .put("Principal", "*")
      .put("Resource", "arn:aws:s3:::" + ossConfigProperties.getBucketName());
    bucketPolicy.putArray("Action")
      .add("s3:GetBucketLocation")
      .add("s3:ListBucket")
      .add("s3:ListBucketMultipartUploads");      
    ObjectNode contentPolicy = mapper.createObjectNode()
      .put("Effect", "Allow")
      .put("Principal", "*")
      .put("Resource", "arn:aws:s3:::" + ossConfigProperties.getBucketName() + "/*");
    contentPolicy.putArray("Action")
      .add("s3:AbortMultipartUpload")
      .add("s3:DeleteObject")
      .add("s3:GetObject")
      .add("s3:ListMultipartUploadParts")
      .add("s3:PutObject");
    ObjectNode policy = mapper.createObjectNode()
      .put("Version", "2012-10-17");
    policy.putArray("Statement")
        .add(bucketPolicy)
        .add(contentPolicy);
    return policy.toString();
    // @formatter:on
  }
}
