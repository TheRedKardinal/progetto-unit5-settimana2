import type { Messaggio } from '../types';
import { formattaOra } from '../utils/format';

interface MessageBubbleProps {
  messaggio: Messaggio;
  mio: boolean;
}

export default function MessageBubble({ messaggio, mio }: MessageBubbleProps) {
  return (
    <div className={`bc-bubble-row ${mio ? 'mine' : ''}`}>
      <div className={`bc-bubble ${mio ? 'mine' : 'theirs'}`}>
        <div>{messaggio.testo}</div>
        <div className="bc-bubble-meta">
          <span>{formattaOra(messaggio.createdAt)}</span>
          {mio && <span>{messaggio.letto ? '✓✓ letto' : '✓ inviato'}</span>}
        </div>
      </div>
    </div>
  );
}
