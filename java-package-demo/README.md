# Java パッケージサンプル — 環境構築・実行ガイド

別のマシンでも同じようにビルド・実行できるようにするための手順書。
OS は Ubuntu (24.04) を想定。macOS / WSL でもほぼ同様に進められる。

---

## 1. 前提環境

| ツール | 必要なバージョン | 備考 |
|---|---|---|
| Java (JDK) | **17 以上**（推奨: 21） | Gradle 9.x の実行に必須 |
| Gradle | 9.x（このプロジェクトは 9.5.1 で確認） | Wrapper 経由なら手動インストール不要 |

> **重要**: Gradle 9.0 以降は Gradle 実行用の JVM に **Java 17 以上**が必須。
> Java 11 以下では動かないので注意。

---

## 2. Java のインストールと切り替え

### インストール

```bash
sudo apt-get update
sudo apt-get install -y openjdk-21-jdk
```

### バージョン確認

```bash
java -version
```

`openjdk version "21.x.x"` のように表示されれば OK。

### 複数の Java が入っている場合の切り替え

```bash
sudo update-alternatives --config java
```

表示された一覧から Java 21 の番号を選ぶ。

### ハマりどころ: バージョンが切り替わらない

`update-alternatives` で選んでも `java -version` が変わらない場合、
環境変数 `JAVA_HOME` が古い Java を指していることが原因のことが多い。

```bash
# 確認
echo $JAVA_HOME
```

Java 11 などを指していたら、設定ファイル（~/.bashrc など）を修正する。

```bash
# どのファイルに書かれているか探す
grep -l "JAVA_HOME" ~/.bashrc ~/.profile ~/.bash_profile /etc/environment 2>/dev/null

# ~/.bashrc に書かれていた場合の一括置換
sed -i 's|java-11-openjdk-amd64|java-21-openjdk-amd64|g' ~/.bashrc

# 反映
source ~/.bashrc
echo $JAVA_HOME   # Java 21 を指していれば OK
java -version
```

> `/etc/environment` に書かれていた場合は `sudo` で編集し、
> ログインし直す（または新しいターミナルを開く）必要がある。

---

## 3. Gradle について

### Gradle Wrapper を使う（推奨）

このプロジェクトには **Gradle Wrapper**（`gradlew` / `gradlew.bat`）が含まれている。
Wrapper を使えば **Gradle を手動インストールしなくても**、
プロジェクトに紐づいた正しいバージョンの Gradle が自動でダウンロード・使用される。

```bash
./gradlew build      # Linux / macOS
gradlew.bat build    # Windows
```

> 別マシンでも `./gradlew` を使う限り、Gradle のバージョン差による問題が起きにくい。
> **基本的に `gradle` ではなく `./gradlew` を使うこと。**

### 初回実行は時間がかかる

初回は Gradle 本体（約 100MB）のダウンロードが走るため数分かかることがある。
2 回目以降はキャッシュが効き、数秒〜数百ミリ秒で完了する。

### （任意）Gradle を手動で入れたい場合

新規にプロジェクトを作る（`gradle init` を使う）場合は Gradle 本体が必要。
SDKMAN を使うのが手軽。

```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install gradle
gradle -version
```

---

## 4. プロジェクト構成

```
java-package-demo/
├── gradlew                 # Gradle Wrapper (Unix)
├── gradlew.bat             # Gradle Wrapper (Windows)
├── settings.gradle
├── gradle/
│   └── wrapper/            # Wrapper の設定とバージョン情報
└── app/
    ├── build.gradle        # ビルド設定（依存・メインクラス等）
    └── src/
        ├── main/java/com/example/
        │   ├── model/Product.java        # package com.example.model
        │   ├── service/CartService.java  # package com.example.service
        │   └── app/Main.java             # package com.example.app（エントリーポイント）
        └── test/java/...                 # テストコード
```

### パッケージとディレクトリの対応（重要）

Java では **パッケージ名 = ディレクトリ構造** が一致している必要がある。
`app/src/main/java/` が起点（ソースルート）で、その下の階層がパッケージ名になる。

| ファイルの場所 | 必要な package 宣言 |
|---|---|
| `.../java/com/example/model/Product.java` | `package com.example.model;` |
| `.../java/com/example/service/CartService.java` | `package com.example.service;` |
| `.../java/com/example/app/Main.java` | `package com.example.app;` |

一致していないと `does not match the expected package` エラーになる。

---

## 5. build.gradle のポイント

`gradle init`（Application / Java / Groovy DSL）で生成されたものをベースに、
以下を設定する。

```gradle
plugins {
    id 'application'
}

repositories {
    mavenCentral()        // jcenter() は廃止済みなので使わない
}

dependencies {
    // 外部ライブラリが必要ならここに記述
    // implementation 'グループ:名前:バージョン'
    // testImplementation '...'
}

application {
    mainClass = 'com.example.app.Main'   // エントリーポイントを指定
}

// 日本語の文字化け対策（環境によって必要）
tasks.withType(JavaCompile) {
    options.encoding = 'UTF-8'
}

tasks.named('run') {
    jvmArgs = ['-Dfile.encoding=UTF-8', '-Dstdout.encoding=UTF-8']
}
```

### 記法に関する注意（古い情報との違い）

| 古い記法（使わない） | 現在の記法 |
|---|---|
| `apply plugin: 'java'` | `plugins { id '...' }` ブロック |
| `jcenter()` | `mavenCentral()` |
| `compile '...'` | `implementation '...'` |
| `testCompile '...'` | `testImplementation '...'` |

> 古い Gradle（apt で入る 4.x 等）や古いチュートリアルでは旧記法が使われている。
> Gradle 9.x では上記の新記法を使うこと。

---

## 6. ビルドと実行

```bash
# ビルド（コンパイル + JAR 生成 + テスト）
./gradlew build

# 実行
./gradlew run

# ビルド成果物を削除（クリーン）
./gradlew clean
```

### 実行結果の例

```
=== Javaパッケージ サンプル ===
 [追加] りんご をカートに追加しました
 [追加] 牛乳 をカートに追加しました
 [追加] 食パン をカートに追加しました
  --- カートの中身 ---
  ・りんご : 150円
  ・牛乳 : 200円
  ・食パン : 180円
  合計: 530円
  -------------------
=== 完了 ===
BUILD SUCCESSFUL
```

---

## 7. エディタ（VS Code）でエラー表示が出る場合

コマンドライン（`./gradlew build`）では成功するのに、
エディタ上で `cannot be resolved` や `does not match the expected package`
が表示される場合は、エディタ側のキャッシュずれが原因のことが多い。

VS Code（Java 拡張）での対処:

1. コマンドパレットを開く（`Ctrl+Shift+P` / macOS は `Cmd+Shift+P`）
2. **`Java: Clean Java Language Server Workspace`** を実行
3. 再読み込みの確認に従う

また、VS Code で開くフォルダは **`settings.gradle` があるプロジェクトのルート**にすること。
`app` などサブフォルダだけを開くと Gradle プロジェクトとして認識されない。

---

## 8. 別マシンでのセットアップ手順（まとめ）

新しいマシンでこのプロジェクトを動かす最短手順。

```bash
# 1. Java 17 以上を用意
sudo apt-get install -y openjdk-21-jdk
java -version                      # 21.x を確認

# 2. JAVA_HOME が古い Java を指していないか確認（必要なら修正）
echo $JAVA_HOME

# 3. プロジェクトを取得（git clone など）してディレクトリへ移動
cd java-package-demo

# 4. Wrapper 経由で実行（Gradle は自動ダウンロードされる）
./gradlew run
```

これだけで動く。Gradle 本体の手動インストールは不要。

---

## 補足: チェックリスト

- [ ] `java -version` が 17 以上（推奨 21）
- [ ] `echo $JAVA_HOME` が新しい Java を指している
- [ ] プロジェクトのルート（`settings.gradle` のある場所）で作業している
- [ ] `gradle` ではなく `./gradlew` を使っている
- [ ] `build.gradle` の `mainClass` が正しいクラスを指している
- [ ] パッケージ宣言とディレクトリ構造が一致している