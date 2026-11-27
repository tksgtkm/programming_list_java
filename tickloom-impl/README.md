# tickloom-impl 実行方法メモ

unmeshjoshi/tickloom 相当を自作するプロジェクトの、ビルド・テストの実行方法まとめ（ステップ 1〜2 時点）。

## 前提環境

| 項目 | 内容 |
|---|---|
| OS | Ubuntu |
| Java | 21（`JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64`） |
| Gradle | SDKMAN の Gradle 9.5.1 で wrapper を生成し、以降は `./gradlew` を使う |
| テスト | JUnit 5（Jupiter） |
| 依存 | Jackson databind 2.17.0 |

## ディレクトリ構成

```
~/github/
├── _ref/tickloom/          # 本家（読むだけ）
└── tickloom-impl/          # 自作
    ├── settings.gradle
    ├── build.gradle
    ├── gradlew / gradle/
    └── src/
        ├── main/java/com/tickloom/
        │   ├── Tickable.java
        │   ├── ProcessId.java
        │   ├── util/Timeout.java
        │   ├── messaging/{MessageType,Message}.java
        │   └── network/{PeerType,MessageCodec,JsonMessageCodec}.java
        ├── test/java/com/tickloom/
        │   ├── util/TimeoutTest.java
        │   ├── messaging/{MessageTest,MessageTypeTest}.java
        │   └── network/JsonMessageCodecTest.java
        └── testFixtures/java/com/tickloom/   # ステップ3以降（SimulatedNetwork, Cluster）
```

## 初回セットアップ

```bash
# 本家を参照用に clone
git clone https://github.com/unmeshjoshi/tickloom.git ~/github/_ref/tickloom

# 自作リポジトリ
mkdir -p ~/github/tickloom-impl && cd ~/github/tickloom-impl
git init
mkdir -p src/main/java/com/tickloom src/test/java/com/tickloom src/testFixtures/java/com/tickloom
```

`settings.gradle`

```groovy
rootProject.name = 'tickloom-impl'
```

`build.gradle`

```groovy
plugins {
    id 'java-library'
    id 'java-test-fixtures'   // SimulatedNetwork / Cluster 用
}

group = 'com.tickloom'
version = '0.1.0-SNAPSHOT'

java {
    toolchain { languageVersion = JavaLanguageVersion.of(21) }
}

repositories { mavenCentral() }

dependencies {
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.17.0'
    testImplementation platform('org.junit:junit-bom:5.10.2')
    testImplementation 'org.junit.jupiter:junit-jupiter'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

test {
    useJUnitPlatform()
    testLogging { events 'passed', 'skipped', 'failed' }
}
```

```bash
gradle wrapper                 # wrapper を生成（以降は ./gradlew）
printf "build/\n.gradle/\n*.iml\n.idea/\n" > .gitignore
./gradlew build                # 空でも通ることを確認
git add . && git commit -m "init"
```

## ビルド・テスト

```bash
./gradlew build        # コンパイル + テスト + jar
./gradlew test         # テストのみ
./gradlew clean test   # ビルド成果物を消してからテスト
```

## テストを絞って実行する（`--tests`）

```bash
# クラス単位
./gradlew test --tests 'com.tickloom.util.TimeoutTest'
./gradlew test --tests 'com.tickloom.messaging.MessageTypeTest'
./gradlew test --tests 'com.tickloom.messaging.MessageTest'
./gradlew test --tests 'com.tickloom.network.JsonMessageCodecTest'

# メソッド単位
./gradlew test --tests 'com.tickloom.util.TimeoutTest.firesExactlyAfterDurationTicks'

# パッケージ単位
./gradlew test --tests 'com.tickloom.util.*'
./gradlew test --tests 'com.tickloom.messaging.*'
```

| 指定 | 対象 |
|---|---|
| `--tests 'com.tickloom.messaging.MessageTypeTest'` | 1 クラス |
| `--tests 'MessageTypeTest'` | クラス名のみでも可 |
| `--tests '*MessageType*'` | ワイルドカード |
| `--tests 'com.tickloom.messaging.*'` | パッケージ |
| `--tests 'MessageTypeTest.equalityIsByName'` | 1 メソッド |
| `--tests A --tests B` | 複数指定 |

### 毎回テストを再実行したい場合

変更がないと Gradle は前回結果を再利用してテストをスキップする（`UP-TO-DATE`）。

```bash
./gradlew test --tests 'MessageTypeTest' --rerun
```

## 進め方（ステップごとの流れ）

1. テストを先に書く → `./gradlew test` がコンパイルエラーになるのを確認
2. 実装を書く → 対象テストだけ `--tests` で実行して通す
3. 全体を `./gradlew test` で確認
4. コミット

```bash
# ステップ1: Tickable / Timeout
./gradlew test --tests 'com.tickloom.util.*'
git add . && git commit -m "Add Tickable and tick-based Timeout"

# ステップ2: Message / MessageType / JSON コーデック
./gradlew test
git add . && git commit -m "Add Message, MessageType and JSON codec"
```

## 注意点

- `Message` は `byte[] payload` を持つ record なので、`equals` は配列を参照比較する。テストでは `assertArrayEquals` で payload を比べる。
- `MessageType.of(null)` は `NullPointerException`、`MessageType.of(" ")` は `IllegalArgumentException`。
- `ProcessId` は JSON で `"n1"` の文字列として出力する（本家は `{"name":"n1"}`）。
- VS Code 上のエラー表示はキャッシュの問題のことがある。`./gradlew build` が通るかを正とする。

## 次のステップ

3. `Network` インターフェース + `SimulatedNetwork`（testFixtures に配置。同一 tick 内の FIFO 順、遅延、パーティション）

src/main/java/com/tickloom/
├── Tickable.java
├── ProcessId.java
├── util/Timeout.java
├── messaging/{MessageType,Message}.java
└── network/{PeerType,MessageCodec,JsonMessageCodec}.java
src/test/java/com/tickloom/
├── util/TimeoutTest.java
├── messaging/MessageTest.java
└── network/JsonMessageCodecTest.java