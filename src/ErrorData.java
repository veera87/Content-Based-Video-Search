public class ErrorData {
        public float hError;
        public float yError;
        public float mError;
        public float averageError;
        public int startIndex;
        public int videoIndex;
        public int audioError;
        
        
        public ErrorData() {
                hError = 0;
                yError = 0;
                mError = 0;
                averageError = 0;
                startIndex = 0;
                videoIndex = 0;
                audioError = 0;
        }
        
        public ErrorData(float h, float y, float m,  float avg, int si, int vi) {
                hError = h;
                yError = y;
                mError = m;
                averageError = avg;
                startIndex = si;
                videoIndex = vi;
                audioError = 0;
        }
        
        public void Copy(ErrorData ed) {
                hError = ed.hError;
                yError = ed.yError;
                mError = ed.mError;
                averageError = ed.averageError;
                startIndex = ed.startIndex;
                videoIndex = ed.videoIndex;
        }
        
}