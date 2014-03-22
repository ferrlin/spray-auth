package scalapenos.spray.auth

import java.nio.charset.StandardCharsets._
import java.security._
import javax.crypto._
import javax.crypto.spec._

import base64.Encode.{ apply => toBase64 }
import base64.Encode.{ urlSafe => toBase64UrlSafe }

import base64.Decode.{ apply => fromBase64 }
import base64.Decode.{ urlSafe => fromBase64UrlSafe }


object CryptoSupport {
  private val RandomSource = new SecureRandom()
  private val HashPartSeparator = ":"
  private val DefaultNrOfPasswordHashIterations = 2000
  private val SizeOfPasswordSaltInBytes = 16
  private val SizeOfPasswordHashInBytes = 32

  // ==========================================================================
  // Password Hashing and validation
  // ==========================================================================

  def generateToken: String = generateToken(32) // default to 256 bits
  def generateToken(lengthInBytes: Int) = new String(toBase64UrlSafe(generateRandomBytes(lengthInBytes)), UTF_8)

  def generateRandomBytes(length: Int): Array[Byte] = {
    val keyData = new Array[Byte](length)
    RandomSource.nextBytes(keyData)
    keyData
  }


  // ==========================================================================
  // Password Hashing and validation
  // ==========================================================================

  // This Scala implementation of password hashing was inspired by:
  // https://crackstation.net/hashing-security.htm#javasourcecode
  def hashPassword(password: String): String = hashPassword(password, generateRandomBytes(SizeOfPasswordSaltInBytes))
  def hashPassword(password: String, salt: Array[Byte]): String = hashPassword(password, salt, DefaultNrOfPasswordHashIterations)
  def hashPassword(password: String, salt: Array[Byte], nrOfIterations: Int): String = {
    val hash = pbkdf2(password, salt, nrOfIterations)
    val salt64 = new String(toBase64UrlSafe(salt))
    val hash64 = new String(toBase64UrlSafe(hash))

    s"${nrOfIterations}${HashPartSeparator}${hash64}${HashPartSeparator}${salt64}"
  }

  def validatePassword(password: String, hashedPassword: String): Boolean = {
    /** Compares two byte arrays in length-constant time to prevent timing attacks. */
    def slowEquals(a: Array[Byte], b: Array[Byte]): Boolean = {
      var diff = a.length ^ b.length;
      for (i <- 0 until math.min(a.length, b.length)) diff |= a(i) ^ b(i)
      return diff == 0
    }

    val hashParts = hashedPassword.split(HashPartSeparator)

    if (hashParts.length != 3) return false
    if (!hashParts(0).forall(_.isDigit)) return false

    val nrOfIterations = hashParts(0).toInt // this will throw a NumberFormatException for non-Int numbers...
    val hash = fromBase64UrlSafe(hashParts(1))
    val salt = fromBase64UrlSafe(hashParts(2))

    if (hash.isLeft || salt.isLeft) return false
    if (hash.right.get.length == 0 || salt.right.get.length == 0) return false

    val calculatedHash = pbkdf2(password, salt.right.get, nrOfIterations)

    slowEquals(calculatedHash, hash.right.get)
  }

  private def pbkdf2(password: String, salt: Array[Byte], nrOfIterations: Int): Array[Byte] = {
    val keySpec = new PBEKeySpec(password.toCharArray(), salt, nrOfIterations, SizeOfPasswordHashInBytes * 8)
    val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")

    keyFactory.generateSecret(keySpec).getEncoded()
  }


  // ==========================================================================
  // Hmac Calculation
  // ==========================================================================

  trait Bytes {
    def bytes: Array[Byte]

    lazy val asBase64 = toBase64(bytes)
    lazy val asBase64String = new String(asBase64, UTF_8)

    lazy val asBase64UrlSafe = toBase64UrlSafe(bytes)
    lazy val asBase64UrlSafeString = new String(asBase64UrlSafe, UTF_8)
  }

  case class Hmac(bytes: Array[Byte]) extends Bytes {
    override def toString = s"Hmac(${asBase64String})"
  }

  case class Key(bytes: Array[Byte]) extends Bytes {
    override def toString = s"Key(${asBase64String})"
  }

  object Key {
    def apply(data: String): Key = Key(data.getBytes(UTF_8))

    def fromBase64Bytes(base64Bytes: Array[Byte]): Option[Key] = fromBase64(base64Bytes).right.toOption.map(Key(_))
    def fromBase64String(data: String): Option[Key] = fromBase64Bytes(data.getBytes(UTF_8))

    def fromBase64UrlSafeBytes(base64Bytes: Array[Byte]): Option[Key] = fromBase64UrlSafe(base64Bytes).right.toOption.map(Key(_))
    def fromBase64UrlSafeString(data: String): Option[Key] = fromBase64UrlSafeBytes(data.getBytes(UTF_8))
  }

  def calculateHmacSha1(key: Key, value: String): Hmac = calculateHmac(value, initHmacCalculator(key, "HmacSHA1"))
  def calculateHmacSha256(key: Key, value: String): Hmac = calculateHmac(value, initHmacCalculator(key, "HmacSHA256"))

  private def calculateHmac(value: String, hmacCalculator: Mac): Hmac = calculateHmac(value.getBytes(UTF_8), hmacCalculator)
  private def calculateHmac(value: Array[Byte], hmacCalculator: Mac): Hmac = Hmac(hmacCalculator.doFinal(value))

  private def initHmacCalculator(key: Key, macAlgorithm: String): Mac = {
    val hmacCalculator = Mac.getInstance(macAlgorithm)

    hmacCalculator.init(new SecretKeySpec(key.bytes, macAlgorithm))
    hmacCalculator
  }

}
