from flask import Flask, request, jsonify
from pydub import AudioSegment
import azure.cognitiveservices.speech as speechsdk
import os
import tempfile

app = Flask(__name__)

# Key here
AZURE_SPEECH_KEY = os.getenv('AZURE_SPEECH_KEY')
AZURE_REGION = os.getenv('AZURE_REGION')

# Create TEMP_DIR
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
TEMP_DIR = os.path.join(BASE_DIR, "temp")
os.makedirs(TEMP_DIR, exist_ok=True)

@app.route('/ptt', methods=['POST'])
def convert_audio():
    ogg_path = None
    wav_path = None
    try:
        # Check file
        if 'file' not in request.files:
            return jsonify({"error": "Không nhận được file đầu vào"}), 400

        file = request.files['file']

        if not file.filename.endswith('.ogg'):
            return jsonify({"error": "file không đúng định dạng"}), 400

        # Save OGG
        with tempfile.NamedTemporaryFile(delete=False, suffix='.ogg', dir=TEMP_DIR) as ogg_temp_file:
            ogg_path = ogg_temp_file.name
            file.save(ogg_path)

            # Convert OGG to WAV
            with tempfile.NamedTemporaryFile(delete=False, suffix='.wav', dir=TEMP_DIR) as wav_temp_file:
                wav_path = wav_temp_file.name
                audio = AudioSegment.from_file(ogg_path, format="ogg")
                audio.export(wav_path, format="wav")

                # Call Azure Speech Service
                transcription = transcribe_with_azure(wav_path)

        return jsonify({"transcription": transcription})

    except Exception as e:
        return jsonify({"error": str(e)}), 500

    finally:
        # Clean up temporary files
        if ogg_path and os.path.exists(ogg_path):
            try:
                os.remove(ogg_path)
            except Exception as e:
                print(f"Lỗi khi xóa file OGG: {e}")
        
        if wav_path and os.path.exists(wav_path):
            try:
                os.remove(wav_path)
            except Exception as e:
                print(f"Lỗi khi xóa file WAV: {e}")

def transcribe_with_azure(audio_path):
    speech_config = speechsdk.SpeechConfig(subscription=AZURE_SPEECH_KEY, region=AZURE_REGION)
    speech_config.speech_recognition_language = "vi-VN"
    audio_config = speechsdk.audio.AudioConfig(filename=audio_path)
    speech_recognizer = speechsdk.SpeechRecognizer(speech_config=speech_config, audio_config=audio_config)

    result = speech_recognizer.recognize_once()

    if result.reason == speechsdk.ResultReason.RecognizedSpeech:
        return result.text
    elif result.reason == speechsdk.ResultReason.NoMatch:
        raise Exception("Không nhận diện được giọng nói.")
    elif result.reason == speechsdk.ResultReason.Canceled:
        cancellation_details = result.cancellation_details
        raise Exception(f"Nhận diện giọng nói bị hủy: {cancellation_details.reason}. Lỗi: {cancellation_details.error_details}")

if __name__ == '__main__':
    app.run(host="0.0.0.0", debug=True, port=5000)