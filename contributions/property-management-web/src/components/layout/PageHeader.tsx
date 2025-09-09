import { ReactNode } from 'react';
import { Button } from '../ui/button';
import { Badge } from '../ui/badge';

interface PageHeaderProps {
  title: string;
  subtitle?: string;
  badge?: string;
  badgeVariant?: 'default' | 'secondary' | 'destructive' | 'outline';
  actions?: ReactNode;
  children?: ReactNode;
}

export function PageHeader({ 
  title, 
  subtitle, 
  badge, 
  badgeVariant = 'secondary',
  actions, 
  children 
}: PageHeaderProps) {
  return (
    <div className="border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="px-6 py-4">
        <div className="flex items-center justify-between">
          <div className="space-y-1">
            <div className="flex items-center gap-3">
              <h1 className="font-semibold tracking-tight">{title}</h1>
              {badge && (
                <Badge variant={badgeVariant}>{badge}</Badge>
              )}
            </div>
            {subtitle && (
              <p className="text-muted-foreground">{subtitle}</p>
            )}
          </div>
          
          {actions && (
            <div className="flex items-center gap-2">
              {actions}
            </div>
          )}
        </div>
        
        {children && (
          <div className="mt-4">
            {children}
          </div>
        )}
      </div>
    </div>
  );
}