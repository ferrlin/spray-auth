package scalapenos.spray.auth
package util

import org.specs2.mutable.Specification


class CryptoSupportSpec extends Specification {
  import CryptoSupport._

  "The Password hashing implementation" should {
    "generate different hashes for hashPasword(password) using the default random salt" in {
      val first = hashPassword("password")
      val second = hashPassword("password")

      first !== second
    }

    "generate different hashes for hashPasword(password, salt) when the salts are not the same" in {
      val first = hashPassword("password", generateRandomBytes(16))
      val second = hashPassword("password", generateRandomBytes(16))

      first !== second
    }

    "generate different hashes for hashPasword(password, salt, iterations) when only the number of iterations differs" in {
      val salt = generateRandomBytes(16)
      val first = hashPassword("password", salt, 1000)
      val second = hashPassword("password", salt, 1001)

      first !== second
    }

    "generate the same hash for hashPasword(password, salt) when the salts are the same" in {
      val salt = generateRandomBytes(16)
      val first = hashPassword("password", salt)
      val second = hashPassword("password", salt)

      first === second
    }

    "generate the same hash for hashPasword(password, salt, iterations) when the salts and the number of iterations are the same" in {
      val salt = generateRandomBytes(16)
      val first = hashPassword("password", salt, 1000)
      val second = hashPassword("password", salt, 1000)

      first === second
    }

    "validate a password against the correct hashed password" in {
      val password = "My secret is safe with me"
      val hash = hashPassword(password)
      validatePassword(password, hash) must beTrue
    }

    "not validate a password against an incorrect hashed password" in {
      val password = "My secret is safe with me"
      val hash = hashPassword(password)

      validatePassword(password, "clearly incorrect") must beFalse
      validatePassword(password, "1000:a:b") must beFalse
      validatePassword(password, "A" + hash) must beFalse
      validatePassword(password, "1" + hash) must beFalse
    }
  }
}
