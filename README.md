# SimpleNetherFogFix
SimpleNetherFogFixはGeyserのnether-roof-workaroundをtrueにした際にネザー全体が同じ赤い霧になるのをバイオームごとに異なる霧が発生する(ネザーデフォルトの挙動になる)ようにするシンプルなExtensionsです。

## ！！注意事項！！
このコードはAIのGeminiに政策を手伝ってもらいましたその点をご理解ください。
私はプログラミングの簡単な知識はありますが政策はできないためGeminiには感謝しております。

## コードの概要
このコードはGeyserExtrasにある霧を修正するとゆう機能のコードを解析してネザー全体の霧を修正する機能だけを抽出してシンプルにまとめたExtensions(プラグイン)です。
このExtensionsを制作する際にもとになったコードを制作したGeyserExtrasの開発者に感謝します。

GeyserExtras
https://github.com/GeyserExtras/GeyserExtras

## コードの概要簡単な説明
このコードはGeyserExtras 2.0.0-BETA-11 のcore/src/main/java/dev/letsgoaway/geyserextras/core/ExtrasPlayer.javaの216行目〜218行目にあるコード

if (session.getDimensionType().isNetherLike() && session.camera().fogEffects().contains(DimensionUtils.BEDROCK_FOG_HELL)) {
 　 session.camera().removeFog(DimensionUtils.BEDROCK_FOG_HELL);

を抽出してシンプルにまとめたのがSimpleNetherFogFixです。

## コードの詳細AIからの引用
このコードが何をしているか：
1.判定: プレイヤーが現在「ネザー（またはネザーのような次元）」にいるかを確認します 。
2.霧のチェック: Geyserが標準で適用しようとする「ネザー全体の赤い霧（BEDROCK_FOG_HELL）」が適用されているかを確認します 。
3.解除: もしその霧がかかっていたら、それを**強制的に解除（remove）**します 。
これにより、Geyserが上書きしていた「一律の赤い霧」が消え、Minecraft（統合版クライアント）が本来持っているバイオームごとの霧の描写が復活します。

## 最後に...
なぜこのコードを公開したのかですが元々はネザーの岩盤の上に行ける機能Geyserのnether-roof-workaroundをtrueにした際にネザー全体が同じ赤い霧になるのが
気に入らずどうにかして修正できないかいろいろしておりましたがネザー全体が同じ赤い霧になるのを修正できるものを最初は見つけることができませんでした...
ですが探しに探した際にGeyserExtrasを見つけましたGeyserExtrasはとても素晴らしいプラグインだと思いますが私が求めていたのはシンプルにネザーの霧を修正するものでした...
そこでGeyserExtrasのコードを解析してネザー全体の霧を修正する機能だけを抽出してシンプルにまとめたのがこのプラグインです。

このプラグインExtensionsを公開したのは私のようにシンプルにネザーの霧のみを修正できる機能を求めていた方にも使用していただければと思い公開しました。

## 今後のアップデートなどについて
今後はこのコードをアップデートなどをするのかはわかりませんしGeyserのアップデートで使用できなくなっても修正するかわかりません...
バグ報告などをしても対応は致しませんのでご了承ください。
あくまでもこのコードはメモまたはバックアップやこのようなものを求めていた人向けに公開したものです。
